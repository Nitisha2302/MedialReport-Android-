package com.medicalreport.resource

import android.content.Context
import android.graphics.Typeface

class ResourcesProviderImpl(private val context: Context) : ResourcesProvider {
    override fun getString(id: Int): String {
        return context.getString(id)
    }

    override fun getStringArray(id: Int): Array<String> {
        return context.resources.getStringArray(id)
    }

    override fun getColor(id: Int): Int {
        return context.getColor(id)
    }

    override fun getFont(path: String): Typeface {
        return Typeface.createFromAsset(context.resources.assets, path)
    }

    override fun getContext(): Context {
        return context
    }

    override fun getInt(id: Int): Int {
        return context.resources.getInteger(id)
    }
}