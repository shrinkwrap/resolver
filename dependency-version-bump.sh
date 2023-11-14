file="$(pwd)/pom.xml"
while read -r line
do
    if [[ $line == "<version."* ]]; then
        value="${line%<*}"; value="${value#*>}"
        dependency="${line%%>*}"; dependency="${dependency:1}"
        printf "Checking version of ${dependency#*.} with value ${value} in other pom.xmls\n"
        find . -type f -name "*pom*.xml" -exec sed -i'' -e "s/<${dependency}>.*<\/${dependency}>/<${dependency}>${value}<\/${dependency}>/" {} +
    fi
    if [[ $line == "</properties>" ]]; then
        break
    fi
done <"$file"
