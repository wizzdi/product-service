package com.flexicore.product.service;

import com.flexicore.product.config.Config;
import com.google.crypto.tink.*;
import com.google.crypto.tink.aead.AeadFactory;
import com.google.crypto.tink.aead.AeadKeyTemplates;
import com.google.crypto.tink.config.TinkConfig;
import com.google.crypto.tink.proto.KeyTemplate;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EncryptionService {
    private static Aead aead;
    private static AtomicBoolean init=new AtomicBoolean(false);

    public static void initEncryption(Logger logger) {
        if(init.compareAndSet(false,true)){
            try {
                TinkConfig.register();
                File keysetFile = new File(Config.getKeySetFilePath());
                KeysetHandle keysetHandle;
                if (!keysetFile.exists()) {
                    KeyTemplate keyTemplate = AeadKeyTemplates.AES128_GCM;
                    keysetHandle = KeysetHandle.generateNew(keyTemplate);
                    CleartextKeysetHandle.write(keysetHandle, JsonKeysetWriter.withFile(keysetFile));
                } else {
                    keysetHandle = CleartextKeysetHandle.read(JsonKeysetReader.withFile(keysetFile));
                }
                aead = AeadFactory.getPrimitive(keysetHandle);


            } catch (Exception e) {
                logger.log(Level.SEVERE, "failed loading keyHandle", e);
            }

        }


    }

    public static Aead getAead() {
        return aead;
    }
}
