# Sky Islands（空岛世界）

在 Minecraft **创建新世界**界面新增可选择的空岛世界类型：地形是无限虚空 + 出生点起始岛，**生物群系保持原版正常分布**，**种子照常随机或输入**。支持 1.21.1 / 1.21.11 × Fabric / NeoForge 四个构建。

## 内置世界类型

| 类型 | 起始内容 |
|---|---|
| **经典空岛 Classic** | 5×5 双层草方块岛 + 中心基岩锚 + 橡树 + 箱子（熔岩桶、冰）——经典圆石机开局 |
| **原教旨空岛 Old-School** | 3×3 小岛 + 橡树 + 箱子（熔岩桶、冰×2、甘蔗、南瓜/西瓜种子、面包、仙人掌）——还原 2011 曲线 |
| **空岛群岛 Archipelago** | 经典主岛 + 四座主题小岛（沙岛+仙人掌、雪岛、下界岩岛、末地石岛），探索向 |
| **单块空岛 Single Block** | 一块草方块 + 一株树苗——硬核挑战 |

所有类型的共同点：

- **只有主世界**（没有下界/末地），虚空之下是彻底的空；
- 生物群系由原版 `multi_noise` overworld 预设驱动——生物群系分布与同种子普通世界一致；
- 种子与普通世界完全同管线：创建界面随机/输入均可；
- 起始岛在世界首次启动时生成一次（saved-data 标记保证**永不重建**，箱子被搬空也不会在重启后复活）。

## 使用

**单人**：把对应 jar 放进 `mods/`，创建新世界时点"世界类型"按钮轮换到 Sky Islands / 经典空岛 等即可。

**专用服务器**：`server.properties` 里写（注意 properties 里冒号可转义）：

```properties
level-type=skyislands\:classic
```

可选值：`skyislands:classic` / `skyislands:oldschool` / `skyislands:archipelago` / `skyislands:single`。世界首次启动时自动建岛并把世界出生点设到岛上（8, 64, 8）。

## 构建

每个子项目独立构建（与组织内其他 mod 相同的约定）：

```bash
cd 1.21.1/fabric   # 或 1.21.1/neoforge、1.21.11/fabric、1.21.11/neoforge
GRADLE_USER_HOME=~/.gradle-skyislands ./gradlew build
# 产物：build/libs/skyislands-<loader>-<mc>-<version>.jar
```

> 若机器配置了全局 Gradle 镜像 init 脚本（如阿里云），NeoForge 依赖会解析失败，务必用隔离的 `GRADLE_USER_HOME`。

## 技术实现（简要）

- 世界类型 = 数据包 world preset（`data/skyislands/worldgen/world_preset/*.json`）+ 追加进 `#minecraft:normal` 标签，出现在原版世界类型轮换器；
- 虚空地形 = 自定义 `noise_settings`（`final_density` 恒 0、`default_fluid`=air、`spawn_target` 空）；1.21.11 的 noise_router 键 `preliminary_surface_level` 与 1.21.1 的 `initial_density_without_jaggedness` 差异按版本各维护一份；
- 起始岛 = 服务端 `ServerStarted` 时由 mod 代码放置（固定坐标、确定性布局），随后把世界出生点设到岛上；`SavedData` 一次性标记防重建。

## 验证记录

- 1.21.1 Fabric 专用服务器 E2E：岛方块/基岩锚/树/箱子、箱内物资、远处虚空无地形、出生群系非 void、输入种子生效（10/10）；机器人加入直接出生在岛上（2/2）；重启后 `island already present, skipping` 不重建。
- 1.21.11 Fabric 专用服务器：同项断言通过，`level-type=skyislands:classic` + 种子生效，出生区块生成 ~3.5s。
- NeoForge 两侧编译通过（未做启动冒烟）。

## License

MIT
