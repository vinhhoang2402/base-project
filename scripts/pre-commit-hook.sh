#!/bin/bash

echo "🔍 Pre-commit checks..."

# 1. KtLint format check
echo "1️⃣  Running KtLint..."
./gradlew ktlintFormat > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "❌ KtLint check failed"
    exit 1
fi
echo "   ✅ KtLint passed"

# 2. Check for hardcoded secrets
echo "2️⃣  Checking for secrets..."
git diff --cached 2>/dev/null | grep -iE "(password|token|api[_-]?key|secret)" && {
    echo "❌ Found potential secrets in commit!"
    echo "   Remove sensitive data and try again"
    exit 1
}
echo "   ✅ No secrets found"

# 3. Check file size
echo "3️⃣  Checking file sizes..."
git diff --cached --name-only --diff-filter=A,M 2>/dev/null | while read file; do
    if [ -f "$file" ]; then
        lines=$(wc -l < "$file")
        if [ $lines -gt 500 ]; then
            echo "⚠️  Warning: File '$file' has $lines lines (target: < 500)"
        fi
    fi
done
echo "   ✅ File sizes checked"

# 4. Quick Android pattern check
echo "4️⃣  Quick Android pattern scan..."
git diff --cached --name-only | grep -E "\.kt$" | while read file; do
    # Check for generic model names (Data-1 violation)
    if grep -q "class Response\|class Entity\|class Model" "$file" 2>/dev/null; then
        echo "⚠️  $file: Generic model names detected (should be feature-specific)"
    fi
    
    # Check for GlobalScope (MEM-5 violation)
    if grep -q "GlobalScope.launch" "$file" 2>/dev/null; then
        echo "❌ $file: GlobalScope.launch detected (use viewModelScope instead)"
    fi
    
    # Check for missing binding cleanup (MEM-1 violation)
    if grep -q "class.*Fragment" "$file" && ! grep -q "_binding = null" "$file" 2>/dev/null; then
        echo "⚠️  $file: Fragment might need binding cleanup in onDestroyView"
    fi
done
echo "   ✅ Android patterns checked"

echo ""
echo "✅ Pre-commit checks passed!"
echo ""
echo "📋 Next steps:"
echo "   1. Push to remote (GitHub Actions runs android-review)"
echo "   2. Run /code-review medium --fix for detailed review"
echo "   3. Address Blockers before merge"
exit 0
