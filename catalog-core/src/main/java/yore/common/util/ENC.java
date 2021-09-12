/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package yore.common.util;

import com.baomidou.dynamic.datasource.toolkit.CryptoUtils;

/**
 * 明文转密文(如果未设置默认使用全局的)
 *
 * Created by Yore
 */
public class ENC {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Please enter the string to be encrypted!");
            System.exit(0);
        }
        try {
            String encryptStr = CryptoUtils.encrypt( args[0]);
            System.out.println("Plaintext：" + args[0]);
            System.out.println("Ciphertext：" + encryptStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
