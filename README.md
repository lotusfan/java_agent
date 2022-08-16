# java_agent for elapsed time log
日志打印
### 使用

> java -javaagent:{agent_jar_path}/{agent_jar.jar}={log_path}/{log_file},{keyword},{min_time_ms} -noverify -jar {target.jar}

|parameter|required|description|
| --- | --- | --- |
|agent_jar_path|true|带依赖关系的agent jar包路径|
|agent_jar.jar|true|带依赖关系agent jar全名|
|log_path|true|日志输出路径|
|log_file|true|日志输出文件名|
|keyword|true|className#indexOf的关键词，注：全类名是用/分隔，如，java/lang/Object;|
|min_time_ms|false|时间单位：毫秒；如果执行时间大于min_time_ms，则输日志；默认值-1，表示所有执行方法都会输出日志|
|target.jar|true|目标执行jar|

### 日志输出格式
```
|-----------------------------------------------------------------------------------------------------------------------------
|com.general.Main2#sss1                                                                              | 2003ms
|com.general.Main2#sss                                                                               | 5007ms ≈5s
|com.general.MainTest#sub1                                                                           | 6013ms ≈6s
|com.general.MainTest#main                                                                           | 9018ms ≈9s
|-----------------------------------------------------------------------------------------------------------------------------
```
### general model
> java -javaagent:/--path--/logEnhance-jar-with-dependencies.jar=/--path--/time.txt,com/general,1 -jar general-1.0-SNAPSHOT.jar
#### 可以通过sort来排序查看
>  grep "54C6B5D5952C458C8599F3F482321CEF" time.txt | sort -n -t"|" -k3
