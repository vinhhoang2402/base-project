#!/bin/bash

FEATURE=$1

if [ -z "$FEATURE" ]; then
    echo "Usage: ./scripts/build-feature.sh feature-name"
    echo ""
    echo "Available features:"
    ls feature/ 2>/dev/null | grep -E "^feature-" | sed 's/^feature-//' || echo "  (none yet)"
    exit 1
fi

echo "🔨 Building feature: $FEATURE..."
./gradlew :feature:feature-$FEATURE:build

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Feature '$FEATURE' built successfully!"
    echo "📊 Run tests:"
    echo "   ./gradlew :feature:feature-$FEATURE:testDebugUnitTest"
else
    echo ""
    echo "❌ Build failed for feature: $FEATURE"
    exit 1
fi
