package com.ibm.gradle.plugin.kubernetes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.gradle.plugin.kubernetes.task.DeployToKubernetes;
import com.ibm.gradle.plugin.kubernetes.task.entity.Volume;
import org.gradle.api.Project;
import org.gradle.internal.impldep.org.junit.Test;
import org.gradle.testfixtures.ProjectBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.gradle.internal.impldep.org.junit.Assert.assertTrue;

public class K8sPluginTest {
    @Test
    public void greeterPluginAddsGreetingTaskToProject() {
        Project project = ProjectBuilder.builder().build();
        project.getPluginManager().apply("com.ibm.esw.kubernetes.spring-boot-application");

        assertTrue(project.getTasks().getByName("deployToKubernetes") instanceof DeployToKubernetes);
    }

  public static void main(String[] args) {
//      Project project = ProjectBuilder.builder().build();
//      project.getPluginManager().apply("com.ibm.esw.kubernetes.spring-boot-application");
//
//      assertTrue(project.getTasks().getByName("deployToKubernetes") instanceof DeployToKubernetes);

      Map<String, String> m = new HashMap<String, String>(){{
          put("volumeFrom" , "secret");
          put("name" , "danube-configuration-cache");
      }};

      new ObjectMapper().convertValue(m, Volume.class);
  }
}
