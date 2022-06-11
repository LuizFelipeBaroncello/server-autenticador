package com.raphaluiz.serverautenticador.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import de.taimos.totp.TOTP;
import lombok.AllArgsConstructor;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.crypto.KDFCalculator;
import org.bouncycastle.crypto.fips.Scrypt;
import org.bouncycastle.util.Strings;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;

@Service
@AllArgsConstructor
public class TwoFactorAuthService {

    public String getTOTPCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);

    }

    public String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) {
        try {
            return "otpauth://totp/"
                    + URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20")
                    + "?secret=" + URLEncoder.encode(secretKey, "UTF-8").replace("+", "%20")
                    + "&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public void createQRCode(String barCodeData, String filePath, int height, int width)
            throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE,
                width, height);
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            MatrixToImageWriter.writeToStream(matrix, "png", out);
        }
    }


    // Adaptado de https://downloads.bouncycastle.org/fips-java/BC-FJA-UserGuide-1.0.2.pdf
    public byte[] useScryptKDF(char[] password,
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

    public static byte[] toByteArray(
            String string)
    {
        byte[]	bytes = new byte[string.length()];
        char[]  chars = string.toCharArray();

        for (int i = 0; i != chars.length; i++)
        {
            bytes[i] = (byte)chars[i];
        }

        return bytes;
    }

}
