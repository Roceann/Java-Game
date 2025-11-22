#!/bin/bash

echo "Lancement des tests unitaires..."
echo ""

./gradlew clean -Pheadless
./gradlew test jacocoTestReport -Pheadless

if [ $? -eq 0 ]; then
    echo ""
    echo "Tous les tests sont passés avec succès"
    
    JACOCO_HTML="core/build/reports/jacoco/test/html/index.html"
    JACOCO_XML="core/build/reports/jacoco/test/jacocoTestReport.xml"
    TEST_REPORT="core/build/reports/tests/test/index.html"
    
    if [ -f "$JACOCO_XML" ]; then
        echo ""
        echo "Couverture de code JaCoCo:"
        
        COVERED=$(grep -oP 'covered="\K[0-9]+' "$JACOCO_XML" | awk '{s+=$1} END {print s}')
        MISSED=$(grep -oP 'missed="\K[0-9]+' "$JACOCO_XML" | awk '{s+=$1} END {print s}')
        
        if [ ! -z "$COVERED" ] && [ ! -z "$MISSED" ]; then
            TOTAL=$((COVERED + MISSED))
            PERCENTAGE=$(awk "BEGIN {printf \"%.2f\", ($COVERED/$TOTAL)*100}")
            
            echo "  Instructions couvertes: ${COVERED} / ${TOTAL}"
            echo "  Pourcentage: ${PERCENTAGE}%"
            
            if (( $(echo "$PERCENTAGE < 70" | bc -l) )); then
                echo "  Attention: Couverture inférieure à 70%"
            fi
        fi
    fi
    
    echo ""
    echo "Ouverture des rapports..."
    
    if [ -f "$JACOCO_HTML" ]; then
        echo "  - Rapport JaCoCo: file://$(pwd)/$JACOCO_HTML"
        xdg-open "$JACOCO_HTML" 2>/dev/null &
    fi
    
    if [ -f "$TEST_REPORT" ]; then
        echo "  - Rapport des tests: file://$(pwd)/$TEST_REPORT"
        sleep 1
        xdg-open "$TEST_REPORT" 2>/dev/null &
    fi
    
    exit 0
else
    echo ""
    echo "Des tests ont échoué"
    
    TEST_REPORT="core/build/reports/tests/test/index.html"
    if [ -f "$TEST_REPORT" ]; then
        echo ""
        echo "Ouverture du rapport d'erreurs..."
        xdg-open "$TEST_REPORT" 2>/dev/null &
    fi
    
    exit 1
fi