package com.caterer.puja

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform