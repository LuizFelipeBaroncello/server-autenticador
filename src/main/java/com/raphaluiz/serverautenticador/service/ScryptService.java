package com.raphaluiz.serverautenticador.service;

import lombok.AllArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.crypto.KDFCalculator;
import org.bouncycastle.crypto.fips.Scrypt;
import org.bouncycastle.util.Strings;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@AllArgsConstructor
public class ScryptService {

    private static Integer COST_PARAMETER = 2048;
    private static Integer BLOCK_SIZE = 8;
    private static Integer PARALLELIZATION_PARAM = 1;

    public String encode(String text) {
        return Hex.encodeHexString(useScryptKDF(text.toCharArray(), System.getenv().get("SALT").getBytes(StandardCharsets.UTF_8), COST_PARAMETER, BLOCK_SIZE, PARALLELIZATION_PARAM));
    }

    private byte[] useScryptKDF(char[] password,
                                      byte [] salt, int costParameter, int blocksize, int parallelizationParam ) {
        KDFCalculator<Scrypt.Parameters> calculator
                = new Scrypt.KDFFactory()
                .createKDFCalculator(
                        Scrypt.ALGORITHM.using(salt, costParameter, blocksize, parallelizationParam,
                                Strings.toUTF8ByteArray(password)));
        byte[] output = new byte[32];
        calculator.generateBytes(output);
        return output;
    }




}
