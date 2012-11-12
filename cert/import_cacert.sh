keytool -importcert -v -trustcacerts \
    -file "root.crt" -alias IntermediateCA \
    -keystore "../res/raw/my_keystore.bks" \
    -provider org.bouncycastle.jce.provider.BouncyCastleProvider \
    -providerpath "bcprov-jdk16-145.jar" -storetype BKS -storepass mysecret
