package com.xenia.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;

import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Scans a SAF document-tree URI for recognised Xbox 360 disc/executable images
 * and returns a list of {@link GameEntry} objects for the UI.
 *
 * Supported extensions: .iso, .xex, .zar, .stfs (XBLA packages)
 */
public final class GameLibraryManager {

    private static final String PREF_LIBRARY_ROOT = "library_root_uri";

    private static final Set<String> SUPPORTED_EXTENSIONS = new HashSet<>(
            Arrays.asList(".iso", ".xex", ".zar", ".stfs"));

    private final Context mContext;
    private final SharedPreferences mPrefs;

    public GameLibraryManager(@NonNull final Context context) {
        mContext = context.getApplicationContext();
        mPrefs   = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public void setLibraryRoot(@NonNull final String uriString) {
        mPrefs.edit().putString(PREF_LIBRARY_ROOT, uriString).apply();
    }

    @NonNull
    public String getLibraryRoot() {
        return mPrefs.getString(PREF_LIBRARY_ROOT, "");
    }

    /**
     * Synchronously scans the configured library root and returns all found
     * game entries. Call on a background thread for large libraries.
     */
    @NonNull
    public List<GameEntry> scanLibrary() {
        final List<GameEntry> results = new ArrayList<>();
        final String rootUriString = getLibraryRoot();
        if (rootUriString.isEmpty()) return results;

        try {
            final Uri treeUri = Uri.parse(rootUriString);
            final Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(
                    treeUri, DocumentsContract.getTreeDocumentId(treeUri));
            scanDirectory(childrenUri, treeUri, results);
        } catch (final Exception e) {
            // URI may have been revoked — silently return empty list.
        }

        return results;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private void scanDirectory(@NonNull final Uri childrenUri,
                                @NonNull final Uri treeUri,
                                @NonNull final List<GameEntry> out) {
        final String[] projection = {
                DocumentsContract.Document.COLUMN_DOCUMENT_ID,
                DocumentsContract.Document.COLUMN_DISPLAY_NAME,
                DocumentsContract.Document.COLUMN_MIME_TYPE,
        };

        try (Cursor cursor = mContext.getContentResolver()
                .query(childrenUri, projection, null, null, null)) {
            if (cursor == null) return;
            while (cursor.moveToNext()) {
                final String docId   = cursor.getString(0);
                final String name    = cursor.getString(1);
                final String mime    = cursor.getString(2);

                if (DocumentsContract.Document.MIME_TYPE_DIR.equals(mime)) {
                    // Recurse one level deep.
                    final Uri subChildren =
                            DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, docId);
                    scanDirectory(subChildren, treeUri, out);
                } else if (hasRecognisedExtension(name)) {
                    final Uri fileUri = DocumentsContract.buildDocumentUriUsingTree(
                            treeUri, docId);
                    out.add(new GameEntry(
                            fileUri.toString(),
                            stripExtension(name),
                            name,
                            null  // icon extraction not yet implemented
                    ));
                }
            }
        } catch (final Exception ignored) {
        }
    }

    private static boolean hasRecognisedExtension(@NonNull final String name) {
        final String lower = name.toLowerCase(java.util.Locale.ROOT);
        for (final String ext : SUPPORTED_EXTENSIONS) {
            if (lower.endsWith(ext)) return true;
        }
        return false;
    }

    private static String stripExtension(@NonNull final String name) {
        final int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(0, dot) : name;
    }
}
