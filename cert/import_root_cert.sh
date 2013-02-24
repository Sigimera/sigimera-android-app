echo "Importing cacert.org root certificate: root.crt"
keytool -importcert -v -trustcacerts \
    -file "root.crt" -alias IntermediateCA \
    -keystore "../res/raw/my_keystore.bks" \
    -provider org.bouncycastle.jce.provider.BouncyCastleProvider \
    -providerpath "bcprov-jdk16-145.jar" -storetype BKS -storepass mysecret

echo "Importing startcom root certificate: ca.pem"
keytool -importcert -v -trustcacerts \
    -file "ca.pem" -alias StartComRoot\
    -keystore "../res/raw/my_keystore.bks" \
    -provider org.bouncycastle.jce.provider.BouncyCastleProvider \
    -providerpath "bcprov-jdk16-145.jar" -storetype BKS -storepass mysecret

echo "Importing startcom class1 certificate: sub.class1.server.ca.pem"
keytool -importcert -v -trustcacerts \
    -file "sub.class1.server.ca.pem" -alias StartComClass1 \
    -keystore "../res/raw/my_keystore.bks" \
    -provider org.bouncycastle.jce.provider.BouncyCastleProvider \
    -providerpath "bcprov-jdk16-145.jar" -storetype BKS -storepass mysecret
