class I18nManager {
    constructor() {
        this.currentLanguage = localStorage.getItem('language') || 'es';
        this.loadLanguage();
    }

    loadLanguage() {
        this.updateTexts();
        this.updateLanguageToggle();
    }

    toggleLanguage() {
        this.currentLanguage = this.currentLanguage === 'es' ? 'en' : 'es';
        localStorage.setItem('language', this.currentLanguage);
        this.updateTexts();
        this.updateLanguageToggle();
    }

    updateTexts() {
        const texts = languages[this.currentLanguage];
        
        // Actualizar todos los elementos con data-i18n
        document.querySelectorAll('[data-i18n]').forEach(element => {
            const key = element.getAttribute('data-i18n');
            if (texts[key]) {
                element.textContent = texts[key];
            }
        });

        // Actualizar placeholders
        document.querySelectorAll('[data-i18n-placeholder]').forEach(element => {
            const key = element.getAttribute('data-i18n-placeholder');
            if (texts[key]) {
                element.placeholder = texts[key];
            }
        });

        // Actualizar labels
        document.querySelectorAll('[data-i18n-label]').forEach(element => {
            const key = element.getAttribute('data-i18n-label');
            if (texts[key]) {
                element.textContent = texts[key];
            }
        });
    }

    updateLanguageToggle() {
        const languageToggle = document.getElementById('languageToggle');
        if (languageToggle) {
            languageToggle.textContent = this.currentLanguage === 'es' ? '🇺🇸' : '🇪🇸';
            languageToggle.title = this.currentLanguage === 'es' ? 'English' : 'Español';
        }
    }
}