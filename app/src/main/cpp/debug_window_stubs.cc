#include "xenia/kernel/xmodule.h"
#include "xenia/debug/ui/debug_window.h"

namespace xe {
namespace debug {
namespace ui {

DebugWindow::DebugWindow(Emulator* emulator,
                         xe::ui::WindowedAppContext& app_context)
    : app_context_(app_context), emulator_(emulator) {}

DebugWindow::~DebugWindow() {}

std::unique_ptr<DebugWindow> DebugWindow::Create(
    Emulator* emulator, xe::ui::WindowedAppContext& app_context) {
  return nullptr;
}

void DebugWindow::OnFocus() {}
void DebugWindow::OnDetached() {}
void DebugWindow::OnExecutionPaused() {}
void DebugWindow::OnExecutionContinued() {}
void DebugWindow::OnExecutionEnded() {}
void DebugWindow::OnStepCompleted(cpu::ThreadDebugInfo* thread_info) {}
void DebugWindow::OnBreakpointHit(cpu::Breakpoint* breakpoint,
                                  cpu::ThreadDebugInfo* thread_info) {}

}  // namespace ui
}  // namespace debug
}  // namespace xe
