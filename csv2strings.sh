#!/bin/bash

CSV_FILE="translations.csv"
OUTPUT_DIR="app/src/main/res"

mkdir -p "$OUTPUT_DIR"

# Read CSV
IFS=',' read -r -a headers < <(head -1 "$CSV_FILE")

# Create strings.xml for each language
for ((i=1; i<${#headers[@]}; i++)); do
    lang="${headers[$i]}"
    lang_dir="$OUTPUT_DIR/values"
    
    if [ "$i" -gt 1 ]; then
        lang_dir="$OUTPUT_DIR/values-$lang"
    fi
    
    mkdir -p "$lang_dir"
    
    {
        echo '<?xml version="1.0" encoding="utf-8"?>'
        echo '<resources>'
        
        tail -n +2 "$CSV_FILE" | while IFS=',' read -r key value1 value2 value3 value4; do
            case $i in
                1) value="$value1" ;;
                2) value="$value2" ;;
                3) value="$value3" ;;
                4) value="$value4" ;;
            esac
            
            value=$(echo "$value" | xargs)
            echo "    <string name=\"$key\">$value</string>"
        done
        
        echo '</resources>'
    } > "$lang_dir/strings.xml"
    
    echo "✓ Generated $lang_dir/strings.xml"
done
