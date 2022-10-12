package com.github.verauvads.mvisetup.recipies

import com.android.tools.idea.wizard.template.Template
import com.android.tools.idea.wizard.template.WizardTemplateProvider

class CustomWizardTemplateProvider : WizardTemplateProvider() {
    override fun getTemplates(): List<Template> {
        return listOf(CustomActivityTemplate)
    }
}
