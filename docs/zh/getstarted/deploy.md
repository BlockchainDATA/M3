# 部署Web模块

Web模块是M3项目等web封装。它提供了一系列REST API用于远程操作主机尚的节点。
更多的管理工具将集成中这个前端中。
部署的过程仅仅需要将下列文件拷贝到tomcat的webapps目录中。

```
routing-web/build/libs/routing-web.war
```

## 注意

目前有部分硬编码中routing-api项目中，暂时不要修改war文件的名称。
稍后会进行改进。