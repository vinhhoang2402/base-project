# 18 - XML Widget Conventions (AppCompat vs Material)

**File Size:** ~1kb | **Load Time:** 5s | **Context:** 110 tokens

---

## Rule

`MainActivity` extends `AppCompatActivity` with theme `Theme.Material3.Light.NoActionBar`.
AppCompat's `LayoutInflater.Factory2` auto-upgrades plain `<TextView>`, `<EditText>`,
`<Button>`, `<CheckBox>`, `<RadioButton>`, `<Spinner>` tags to their AppCompat/Material
equivalents at inflate time — **do not** hand-write fully-qualified
`androidx.appcompat.widget.AppCompatTextView`, it's redundant noise.

## When plain tags are fine

```xml
<!-- ✅ GOOD: plain TextView, auto-upgraded by AppCompat -->
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="@string/register_title"
    android:textAppearance="?attr/textAppearanceHeadlineMedium" />
```

## When you MUST use explicit Material widgets

Interactive/branded components need the real Material class — the auto-inflater
does **not** upgrade these:

```xml
<!-- Buttons -->
<com.google.android.material.button.MaterialButton .../>

<!-- Text input -->
<com.google.android.material.textfield.TextInputLayout ...>
    <com.google.android.material.textfield.TextInputEditText .../>
</com.google.android.material.textfield.TextInputLayout>

<!-- Toolbar -->
<com.google.android.material.appbar.MaterialToolbar .../>

<!-- Cards -->
<com.google.android.material.card.MaterialCardView .../>
```

## Reference examples in this repo

- `feature/feature-home/src/main/res/layout/item_movie.xml`
- `feature/feature-home/src/main/res/layout/fragment_home.xml`
- `feature/feature-auth/src/main/res/layout/fragment_register.xml`

---

**File:** `18-xml-widget-conventions.md`
**Tokens:** 110
