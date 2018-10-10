/*
 * Copyright (c) 2018 David Boissier.
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

package org.codinjutsu.tools.mongo.logic.ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.codinjutsu.tools.mongo.ServerConfiguration;
import org.codinjutsu.tools.mongo.SshTunnelingConfiguration;
import org.codinjutsu.tools.mongo.logic.ConfigurationException;

import java.io.Closeable;
import java.util.LinkedList;
import java.util.List;

public class SshConnection implements Closeable {

    private static final int DEFAULT_TUNNEL_LOCAL_PORT = 9080;

    private final List<Session> sshSessions = new LinkedList<>();

    public static SshConnection create(ServerConfiguration serverConfiguration) {
        return new SshConnection(serverConfiguration.getServerUrls(), serverConfiguration.getSshTunnelingConfiguration());
    }

    public void close() {
        for (Session sshSession : sshSessions) {
            sshSession.disconnect();
        }
    }

    private SshConnection(List<String> serverUrls, SshTunnelingConfiguration sshTunnelingConfiguration) {
        if (sshTunnelingConfiguration == null) {
            throw new IllegalArgumentException("SSH Configuration should be set");
        }

        int localPort = DEFAULT_TUNNEL_LOCAL_PORT;
        for (String serverUrl : serverUrls) {
            Session session = createSshSession(sshTunnelingConfiguration, ServerConfiguration.extractHostAndPort(serverUrl), localPort++);
            sshSessions.add(session);
        }
    }

    private Session createSshSession(SshTunnelingConfiguration sshTunnelingConfiguration,
                                     ServerConfiguration.HostAndPort hostAndPort, int localPort) {
        try {
            JSch jsch = new JSch();

            String proxyHost = sshTunnelingConfiguration.getProxyUrl();
            AuthenticationMethod authenticationMethod = sshTunnelingConfiguration.getAuthenticationMethod();
            String proxyUser = sshTunnelingConfiguration.getProxyUser();
            String password = sshTunnelingConfiguration.getProxyPassword();
            Session session = jsch.getSession(proxyUser, proxyHost);
            if (AuthenticationMethod.PRIVATE_KEY.equals(authenticationMethod)) {
                jsch.addIdentity(sshTunnelingConfiguration.getPrivateKeyPath(),
                        sshTunnelingConfiguration.getProxyPassword());
            } else {
                session.setPassword(password);
            }

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect();

            String remoteMongoHost = hostAndPort.host;
            int remoteMongoPort = hostAndPort.port;
            session.setPortForwardingL(localPort, remoteMongoHost, remoteMongoPort);

            return session;

        } catch (JSchException ex) {
            throw new ConfigurationException(ex);
        }
    }
}
