package com.register.app.util

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import com.register.app.model.CountryCode

object CountryCodeSaver : Saver<CountryCode, String> {
    override fun SaverScope.save(value: CountryCode): String {
        return "${value.name}:${value.code}"
    }

    override fun restore(value: String): CountryCode {
        val (name, code) = value.split(":")
        return CountryCode(name, code)
    }
}