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
package yore.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author Yore Yuan
 */
public abstract class FileWriter {
    private static final Logger LOG = LoggerFactory.getLogger(FileWriter.class);
    protected static BufferedWriter writer;

    /**
     * Initialize writer
     * @param outFilePath Output file path
     */
    protected static void initWriter(String outFilePath) {
        File outFile = new File(outFilePath);
        if (outFile.isDirectory()) {
            LOG.error("The specified {} is a folder, please specify the output file!", outFilePath);
        }
        if (!outFile.exists()) {
            LOG.warn("The output file {} does not exist, it will be created automatically.", outFilePath);
            String outFileDir = outFilePath.substring(0, outFilePath.lastIndexOf(File.separator) + 1);
            File outFileDirFile = new File(outFileDir);
            if (outFileDirFile.mkdirs()) {
                LOG.warn("{} Created successfully!", outFileDir);
            }
        }
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile, true), StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Close writer
     */
    protected static void closeWriter() {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
