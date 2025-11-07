package com.app.noteapp.core.permissions

suspend fun awaitPermission(
    requester: (PermissionRequest, (PermissionResult) -> Unit) -> Unit,
    req: PermissionRequest
): PermissionResult = kotlinx.coroutines.suspendCancellableCoroutine { cont ->
    requester(req) { res ->
        if (cont.isActive) cont.resume(res) {}
    }
}
