if [[ -z "$1" ]]; then
    echo "Please specify a version"
    exit 0
fi

version=$1

oldRtVersion=$(grep "<version>" CeylonRuntime/src/META-INF/plugin.xml | cut -d'>' -f2 | cut -d'<' -f1)
oldVersion=$(grep "<version>" META-INF/plugin.xml | cut -d'>' -f2 | cut -d'<' -f1)

git checkout 141.x-compat || exit -1

sed -i '' -e "s/<version>${oldRtVersion}/<version>${version}/" CeylonRuntime/src/META-INF/plugin.xml
sed -i '' -e "s/<version>${oldVersion}/<version>${version}/" META-INF/plugin.xml

ant clean build -DideaRoot="/Applications/Android Studio.app/Contents/" || exit -1
ant -f buildUpdateSite.xml -Dartifact.output.path="out/installation-packages"

mkdir -p out/installation-packages/legacy
cp out/installation-packages/CeylonIDEA.zip out/installation-packages/legacy
cp out/installation-packages/CeylonRuntime.zip out/installation-packages/legacy
cp out/installation-packages/updatePlugins.xml out/installation-packages/legacy

git checkout CeylonRuntime/src/META-INF/plugin.xml
git checkout META-INF/plugin.xml
git checkout master || exit -1

sed -i '' -e "s/<version>${oldRtVersion}/<version>${version}/" CeylonRuntime/src/META-INF/plugin.xml
sed -i '' -e "s/<version>${oldVersion}/<version>${version}/" META-INF/plugin.xml

ant clean build -DideaRoot="/Applications/IntelliJ IDEA CE.app/Contents/" || exit -1
ant -f buildUpdateSite.xml -Dartifact.output.path="out/installation-packages"

sed -i '' -e "s/<version>${version}/<version>${oldRtVersion}/" CeylonRuntime/src/META-INF/plugin.xml
sed -i '' -e "s/<version>${version}/<version>${oldVersion}/" META-INF/plugin.xml
