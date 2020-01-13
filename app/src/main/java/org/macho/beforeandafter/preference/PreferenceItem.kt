package org.macho.beforeandafter.preference

interface PreferenceElement {
}

class PreferenceItem(val title: Int, val description: Int, val action: () -> Unit): PreferenceElement {
}

class CheckboxPreferenceItem(var title: Int, val description: Int, var isOn: Boolean, val action: (Boolean) -> Unit): PreferenceElement {
}

class SectionHeader(val title: Int): PreferenceElement {
}

class PreferenceFooter(val appVersion: String): PreferenceElement {
}
