/*
 * SnapBundle™ SDK
 * (C) Copyright 2013 Tag Dynamics, LLC (http://tagdynamics.com/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.snapbundle.util;

import com.snapbundle.model.context.IFile;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.hash.Sha256Hash;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public final class HashUtil
{
    public static final int PASSWORD_LENGTH = 12;

    public static final char[] SPECIAL_CHARACTERS = new char[]{'!', '@', '#', '$', '%', '&', '*', '+', '?'};

    private HashUtil()
    {
    }

    public static String createHash(IFile file)
    {
        String contentHash = null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try
        {
            DataOutputStream dos = new DataOutputStream(baos);

            dos.writeUTF(file.getAccount().getUrn());
            dos.writeUTF(file.getUser().getUrn());
            dos.writeUTF(file.getUrn());
            dos.writeUTF(file.getEntityReferenceType().name());
            dos.writeUTF(file.getReferenceUrn());
            dos.writeUTF(file.getMimeType());
            dos.writeLong(file.getTimestamp());
            dos.writeUTF(file.getUrl());

            MessageDigest digest = MessageDigest.getInstance("MD5");

            byte[] hash = digest.digest(baos.toByteArray());

            contentHash = Base64.encodeToString(hash);

        } catch (NoSuchAlgorithmException | IOException e)
        {
            e.printStackTrace();
        }

        return contentHash;
    }

    public static String createHash(char[] chars, InputStream saltStream)
    {
        return (new Sha256Hash(chars, saltStream, 1024)).toBase64();
    }

    public static String createRandomToken(int length)
    {
        long key = -1;

        StringBuilder builder = new StringBuilder(length);

        try
        {
            SecureRandom csprng = SecureRandom.getInstance("SHA1PRNG");

            for (int i = 0; i < length; i++)
            {
                key = (csprng.nextInt(26) + 65);
                builder.append((char) key);
            }
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        return builder.toString();
    }

    public static String createRandomPassword()
    {
        long key = -1;
        String now = Long.toString(System.currentTimeMillis());

        StringBuilder builder = new StringBuilder(PASSWORD_LENGTH);

        builder.append(now.toCharArray()[0]);

        try
        {
            SecureRandom csprng = SecureRandom.getInstance("SHA1PRNG");

            for (int i = 0; i < (PASSWORD_LENGTH - 3); i++)
            {
                key = (csprng.nextInt(26) + 65);
                builder.append((char) key);
            }

            builder.append(now.toCharArray()[now.length() - 1]);

            builder.append(SPECIAL_CHARACTERS[csprng.nextInt(SPECIAL_CHARACTERS.length)]);

        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        return builder.toString();
    }
}
