package com.ime.domain.loader;

import com.ime.domain.SkbTemplate;
import com.ime.domain.SoftKeyboard;

public interface IKeyboardLoader {
    SkbTemplate loadSkbTemplate(int resourceId);
    SoftKeyboard loadKeyboard(int resourceId, int skbWidth, int skbHeight);
}
