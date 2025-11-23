#!/bin/bash
set -e

echo "Lancement: clean, tests, rapport JaCoCo et javadoc..."
echo ""

# Exécute les tâches Gradle (ajoute -Pheadless comme dans ton script)
./gradlew clean -Pheadless test jacocoTestReport javadoc || RC=$?

# Si gradle a renvoyé une erreur, RC sera défini ; sinon RC vide
if [ -n "${RC:-}" ]; then
    echo ""
    echo "Une ou plusieurs tâches Gradle ont échoué (code: ${RC})."
    # Ouvrir le rapport de tests si présent
    TEST_REPORTS=$(find . -type f -path "*/build/reports/tests/test/index.html" 2>/dev/null)
    if [ -n "$TEST_REPORTS" ]; then
        echo ""
        echo "Ouverture des rapports de tests échoués..."
        for f in $TEST_REPORTS; do
            echo "  - file://$(pwd)/$f"
            xdg-open "$f" >/dev/null 2>&1 &
        done
    fi
    exit 1
fi

echo ""
echo "Tâches Gradle réussies."

# Calcul de la couverture JaCoCo à partir de tous les jacocoTestReport.xml trouvés
JACOCO_XMLS=$(find . -type f -path "*/build/reports/jacoco/test/jacocoTestReport.xml" 2>/dev/null)

if [ -n "$JACOCO_XMLS" ]; then
    echo ""
    echo "Couverture JaCoCo (agrégée):"
    COVERED=$(for f in $JACOCO_XMLS; do grep -oP 'covered="\K[0-9]+' "$f" || true; done | awk '{s+=$1} END {print s+0}')
    MISSED=$(for f in $JACOCO_XMLS; do grep -oP 'missed="\K[0-9]+' "$f" || true; done | awk '{s+=$1} END {print s+0}')
    if [ "$COVERED" -gt 0 ] || [ "$MISSED" -gt 0 ]; then
        TOTAL=$((COVERED + MISSED))
        if [ "$TOTAL" -gt 0 ]; then
            PERCENTAGE=$(awk "BEGIN {printf \"%.2f\", ($COVERED/$TOTAL)*100}")
        else
            PERCENTAGE="0.00"
        fi
        echo "  Instructions couvertes: ${COVERED} / ${TOTAL}"
        echo "  Pourcentage: ${PERCENTAGE}%"
        if (( $(echo "$PERCENTAGE < 70" | bc -l) )); then
            echo "  Attention: Couverture inférieure à 70%"
        fi
    else
        echo "  Aucun détail de couverture trouvé dans les fichiers JaCoCo."
    fi
else
    echo ""
    echo "Aucun rapport JaCoCo trouvé."
fi

echo ""
echo "Ouverture des rapports HTML (JaCoCo, tests, javadoc) si présents..."

# Ouvrir tous les index.html JaCoCo trouvés
find . -type f -path "*/build/reports/jacoco/test/html/index.html" -print0 2>/dev/null | while IFS= read -r -d '' f; do
    echo "  - Rapport JaCoCo: file://$(pwd)/$f"
    xdg-open "$f" >/dev/null 2>&1 &
done

# Ouvrir tous les rapports de tests
find . -type f -path "*/build/reports/tests/test/index.html" -print0 2>/dev/null | while IFS= read -r -d '' f; do
    echo "  - Rapport des tests: file://$(pwd)/$f"
    xdg-open "$f" >/dev/null 2>&1 &
done

# Ouvrir toutes les javadoc index.html
find . -type f -path "*/build/docs/javadoc/index.html" -print0 2>/dev/null | while IFS= read -r -d '' f; do
    echo "  - Javadoc: file://$(pwd)/$f"
    xdg-open "$f" >/dev/null 2>&1 &
done

echo ""
echo "Terminé."
exit 0