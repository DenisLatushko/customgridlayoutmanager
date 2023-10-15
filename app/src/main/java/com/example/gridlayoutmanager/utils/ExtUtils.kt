package com.example.gridlayoutmanager.utils

private const val DEFAULT_LOG_TAG = "LOG_TAG"

val Any.LOG_TAG: String
    get() = this::class.simpleName
        ?.let { className ->
            if (className.length > 21) className.substring(0, 20) else className
        } ?: DEFAULT_LOG_TAG