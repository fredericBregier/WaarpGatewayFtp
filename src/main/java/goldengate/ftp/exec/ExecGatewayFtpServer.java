/**
 * Copyright 2009, Frederic Bregier, and individual contributors by the @author
 * tags. See the COPYRIGHT.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3.0 of the License, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package goldengate.ftp.exec;

import goldengate.common.file.filesystembased.FilesystemBasedDirImpl;
import goldengate.common.file.filesystembased.FilesystemBasedFileParameterImpl;
import goldengate.common.file.filesystembased.specific.FilesystemBasedDirJdk5;
import goldengate.common.file.filesystembased.specific.FilesystemBasedDirJdk6;
import goldengate.common.logging.GgInternalLogger;
import goldengate.common.logging.GgInternalLoggerFactory;
import goldengate.common.logging.GgSlf4JLoggerFactory;
import goldengate.ftp.core.config.FtpConfiguration;
import goldengate.ftp.exec.config.FileBasedConfiguration;
import goldengate.ftp.exec.config.R66FileBasedConfiguration;
import goldengate.ftp.exec.control.ExecBusinessHandler;
import goldengate.ftp.exec.data.FileSystemBasedDataBusinessHandler;

import org.jboss.netty.logging.InternalLoggerFactory;

import ch.qos.logback.classic.Level;

/**
 * Exec FTP Server using simple authentication (XML FileInterface based),
 * and standard Directory and FileInterface implementation (Filesystem based).
 *
 * @author Frederic Bregier
 *
 */
public class ExecGatewayFtpServer {
    /**
     * Internal Logger
     */
    private static GgInternalLogger logger = null;

    /**
     * Take a simple XML file as configuration.
     *
     * @param args
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: " +
                    ExecGatewayFtpServer.class.getName() + " <config-file> [<r66config-file>]");
            return;
        }
        InternalLoggerFactory.setDefaultFactory(new GgSlf4JLoggerFactory(
                Level.WARN));
        logger = GgInternalLoggerFactory
                .getLogger(ExecGatewayFtpServer.class);
        String config = args[0];
        FileBasedConfiguration configuration = new FileBasedConfiguration(
                ExecGatewayFtpServer.class, ExecBusinessHandler.class,
                FileSystemBasedDataBusinessHandler.class,
                new FilesystemBasedFileParameterImpl());
        if (!configuration.setConfigurationFromXml(config)) {
            System.err.println("Bad configuration");
            return;
        }
        // Init according JDK
        if (FtpConfiguration.USEJDK6) {
            FilesystemBasedDirImpl.initJdkDependent(new FilesystemBasedDirJdk6());
        } else {
            FilesystemBasedDirImpl.initJdkDependent(new FilesystemBasedDirJdk5());
        }
        if (args.length > 1) {
            if (!R66FileBasedConfiguration.setSimpleClientConfigurationFromXml(args[1])) {
                System.err.println("Bad R66 configuration");
                return;
            }
        }
        // Start server.
        configuration.serverStartup();
        logger.warn("FTP started");
    }

}
