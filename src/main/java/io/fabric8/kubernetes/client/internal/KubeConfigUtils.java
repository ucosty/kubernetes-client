package io.fabric8.kubernetes.client.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.fabric8.kubernetes.api.model.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Helper class for working with the YAML config file thats located in
 * <code>~/.kube/config</code> which is updated when you use commands
 * like <code>osc login</code> and <code>osc project myproject</code>
 */
public class KubeConfigUtils {
  public static Config parseConfig(File file) throws IOException {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    return mapper.readValue(file, Config.class);
  }

  /**
   * Returns the current context in the given config
   */
  public static Context getCurrentContext(Config config) {
    String contextName = config.getCurrentContext();
    if (contextName != null) {
      List<NamedContext> contexts = config.getContexts();
      if (contexts != null) {
        for (NamedContext context : contexts) {
          if (contextName.equals(context.getName())) {
            return context.getContext();
          }
        }
      }
    }
    return null;
  }

  /**
   * Returns the current user token for the config and current context
   */
  public static String getUserToken(Config config, Context context) {
    AuthInfo authInfo = getUserAuthInfo(config, context);
    if (authInfo != null) {
      return authInfo.getToken();
    }
    return null;
  }

  /**
   * Returns the current {@link AuthInfo} for the current context and user
   */
  public static AuthInfo getUserAuthInfo(Config config, Context context) {
    AuthInfo authInfo = null;
    if (config != null && context != null) {
      String user = context.getUser();
      if (user != null) {
        List<NamedAuthInfo> users = config.getUsers();
        if (users != null) {
          for (NamedAuthInfo namedAuthInfo : users) {
            if (user.equals(namedAuthInfo.getName())) {
              authInfo = namedAuthInfo.getUser();
            }
          }
        }
      }
    }
    return authInfo;
  }

  /**
   * Returns the current {@link Cluster} for the current context
   */
  public static Cluster getCluster(Config config, Context context) {
    Cluster cluster = null;
    if (config != null && context != null) {
      String clusterName = context.getCluster();
      if (clusterName != null) {
        List<NamedCluster> clusters = config.getClusters();
        if (clusters != null) {
          for (NamedCluster namedCluster : clusters) {
            if (clusterName.equals(namedCluster.getName())) {
              cluster = namedCluster.getCluster();
            }
          }
        }
      }
    }
    return cluster;
  }
}