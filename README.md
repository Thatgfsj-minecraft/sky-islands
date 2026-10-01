# Sky Islands（空岛世界）

在 Minecraft **创建新世界**界面新增可选择的空岛世界类型：主世界和下界都是无限虚空，**末地保持原版**，**生物群系保持原版正常分布**，**种子照常随机或输入**。

支持 **1.7.10 / 1.12.2 / 1.16.5 / 1.21.1 / 1.21.4 / 1.21.5 / 1.21.8 / 1.21.9 / 1.21.10 / 1.21.11 / 26.1 / 26.2 / 26.3**（24 个构建，覆盖 Fabric / Forge / NeoForge）。

> **26.x 说明**：Minecraft 自 26.1 起改为日期式版本线、不再发布混淆映射，且需要 **Java 25**。26.x 构建与 1.21.x 构建分开维护——worldgen 数据包格式在 26.3 有大改（见"技术实现"），按版本各维护一份。
> **老版本说明**：1.12.2 / 1.7.10 没有数据包，世界类型为纯代码实现（Forge WorldType + 自定义 ChunkGenerator）；1.7.10 需要 Java 8。

## 内置世界类型

| 类型 | 起始内容 |
|---|---|
| **经典空岛 Classic** | 5×5 双层草方块岛 + 中心基岩锚 + 橡树 + 箱子（熔岩桶、冰）——经典圆石机开局 |
| **小型空岛 Small** | 3×3 小岛 + 橡树 + 箱子（熔岩桶、冰×2、甘蔗、南瓜/西瓜种子、面包、仙人掌）——紧凑开局 |
| **单块空岛 Single Block** | 一块草方块 + 一株树苗——硬核挑战 |

## 维度

- **主世界**：无限虚空 + 出生点起始岛；
- **下界**：无限虚空。**第一次有人进入下界时**（原版此刻才初始化维度），定位进入者的坐标，在其脚下生成一次 5×4×3 萤石平台——**全世界只此一次**（saved-data 标记），之后任何地点、任何方式的进入都不再放置任何方块；只填充空气/可替换方块，不碰门框和玩家建筑。生物群系保持原版下界分布；
- **末地**：完全原版（主岛、黑曜石柱、末影龙、外岛照常）。

其他共同点：

- 起始岛在世界首次启动时生成一次（saved-data 标记保证**永不重建**，箱子被搬空也不会在重启后复活）；
- 种子与普通世界完全同管线：创建界面随机/输入均可。

## 使用

**单人**：把对应版本的 jar 放进 `mods/`，创建新世界时点"世界类型"按钮轮换到 经典空岛 / 小型空岛 / 单块空岛 即可。

**专用服务器**：`server.properties` 里写：

```properties
level-type=skyislands\:classic
```

可选值：`skyislands:classic` / `skyislands:oldschool`（小型空岛） / `skyislands:single`。世界首次启动时自动建岛并把世界出生点设到岛上（8, 64, 8）。

## 下载

全部版本的 jar 见 [Releases](../../releases)（一个 release 带全部 24 个构建）：

| MC 版本 | 加载器 | Java | 备注 |
|---|---|---|---|
| 1.7.10 / 1.12.2 | Forge | 8 | 老线；纯代码 WorldType 实现 |
| 1.16.5 | Fabric / Forge | 8 | |
| 1.21.1 – 1.21.11 | Fabric / NeoForge | 21 | 1.21.9 的 NeoForge 官方仅有 beta（21.9.16-beta），已实测可用 |
| 26.1 / 26.2 / 26.3 | Fabric / NeoForge | **25** | 26.3 的 NeoForge 仅有 beta（26.3.0.37-beta） |

老版本验证差异（详见 Release 说明）：1.16.5 双 loader 全功能运行时验证；1.12.2 主世界功能运行时验证（下界平台代码验证，无客户端环境未跑运行时）；1.7.10 主世界功能运行时验证，下界平台为代码验证（1.7.10 无 mineflayer bot 支持）。

## 构建

每个项目目录下单独执行（与组织内其他 mod 相同的约定）：

```bash
cd 1.21.11/fabric   # 或任意 <版本>/<加载器> 子目录
GRADLE_USER_HOME=~/.gradle-skyislands ./gradlew build
# 产物：build/libs/skyislands-<loader>-<mc>-<version>.jar
```

> - 若机器配置了全局 Gradle 镜像 init 脚本（如阿里云），NeoForge 依赖会解析失败，务必用隔离的 `GRADLE_USER_HOME`。
> - **26.x 子项目**：需要 JDK 25（Gradle daemon 与编译都在 25 上），Fabric 侧用 Loom 1.18.2 新插件 id `net.fabricmc.fabric-loom`（无映射行、依赖用 `implementation`），wrapper 为 Gradle 9.7+。
> - **老版本子项目**：JDK 8；1.12.2 = ForgeGradle 2.3 + Gradle 4.10.3，1.7.10 = ForgeGradle 1.2 + Gradle 2.14.1（组合严格钉死，勿升级）。

## 技术实现（简要）

- 世界类型 = 数据包 world preset（`data/skyislands/worldgen/world_preset/*.json`）+ 追加进 `#minecraft:normal` 标签（路径必须是 `data/minecraft/tags/...`），出现在原版世界类型轮换器；
- 虚空地形 = 自定义 `noise_settings`（`final_density` 恒 0、`default_fluid`=air、`spawn_target` 空）；noise_router 密度字段按版本分三份：≤1.21.8 用 `initial_density_without_jaggedness`、1.21.9–26.2 用 `preliminary_surface_level`、26.3 起改名 `chunk_surface_level` 且 router 缩为 8 字段（`surface_rule`→`material_rule`、BlockState JSON 改纯字符串格式）；
- 下界 = 世界预设里的 `minecraft:the_nether` 维度 + 虚空 `noise_settings`（生物群系引用原版 `minecraft:nether` 多噪声预设）；萤石平台 = 玩家**首次**进入下界的事件里按落点生成一次（SavedData 布尔标记，世界生命周期内只一次）；
- 末地 = 世界预设直接引用原版 `minecraft:end` 生成器；
- 起始岛 = 服务端 `ServerStarted` 时由 mod 代码放置（固定坐标、确定性布局），随后把世界出生点设到岛上；SavedData 一次性标记防重建；
- 版本分叉（已实测边界，详见组织 skill）：SavedData 存储在 1.21.4 及以前用 `SavedData.Factory`、1.21.5 起用 `SavedDataType`（26.x 起 id 参数改为 `Identifier`、26.3 起 `DimensionDataStorage` 改名 `SavedDataStorage`）；世界出生点在 1.21.8 及以前用 `setDefaultSpawnPos`、1.21.9 起用 `setRespawnData`；资源定位类 1.21.10 及以前是 `ResourceLocation`、1.21.11 起改名 `Identifier`；Fabric 换维度事件 26.x 起改名 `ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL`。

## 验证记录

- 每个版本的 Fabric 专用服务器冒烟：`level-type` 建岛（草方块/基岩锚/橡树探针 + 负对照）、下界虚空维度全部通过；固定种子复测一致；SavedData 防重建标记跨重启生效。
- 首进下界一次性平台：mineflayer 机器人实测 1.21.1 / 1.21.4 / 1.21.5 / 1.21.8 / 1.21.9 / 1.21.10 / 26.1（协议 775 已支持）——平台三层逐一探针、换地点/重启不再生成、挖掉不重建；26.2 / 26.3 因 minecraft-data 暂缺协议定义跳过 bot 测试（服务端 RCON 断言不受影响，逻辑与 26.1 同源）。
- NeoForge 侧编译验证 + 与 Fabric 同源核心类逐字节一致（`diff -r` 自证）。

## 开源协议 / License

本项目基于 [GPL-3.0](./LICENSE)（GNU 通用公共许可证第 3 版）开源发布。

- 你可以自由地使用、学习、修改和分发本项目的代码；
- 基于本项目修改或二次开发的作品，必须同样以 GPL-3.0 协议开源，并保留相应的版权与许可声明；
- 本项目不提供任何担保，完整条款请参见 [LICENSE](./LICENSE) 文件。
