jdk='/mnt/d/programas/launch4jlinux/amazon-corretto-17.0.0.35.1-linux-x64'

rm -Rf build/linux

mkdir build/linux
mkdir build/linux/jar/
cp build/libs/desktop-1.0.jar build/linux/jar/Charnego-Internatiolaff-Soccer.jar
cp -R $jdk build/linux/jre/
cp -R ../android/assets/* build/linux/

echo "./jre/bin/java -jar jar/Charnego-Internatiolaff-Soccer.jar" > build/linux/Charnego-International-Soccer.sh
chmod +x build/linux/Charnego-International-Soccer.sh

