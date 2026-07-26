#!/bin/bash

# Generate Android strings from CSV using Babelish gem

CSV_FILE="translations.csv"
OUTPUT_DIR="app/src/main/res"

if [ ! -f "$CSV_FILE" ]; then
    echo "❌ $CSV_FILE not found!"
    exit 1
fi

if ! command -v babelish &> /dev/null; then
    echo "📦 Installing babelish gem..."
    gem install babelish
fi

echo "🔨 Generating strings from CSV..."

babelish csv2strings \
    --input=$CSV_FILE \
    --output=$OUTPUT_DIR

if [ $? -eq 0 ]; then
    echo "✅ Generated strings files:"
    find $OUTPUT_DIR -name "strings.xml" -type f 2>/dev/null | sort
else
    echo "❌ Failed to generate strings"
    exit 1
fi
