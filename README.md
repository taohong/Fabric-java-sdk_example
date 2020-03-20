#请先修改maven settings文件
##由于依赖中的org.everit.json.schema并不在maven的中央仓库中，所以默认情况下maven会报错。
##解决办法是：
###1. 在pom.xml中增加repository，地址为https://repository.mulesoft.org/nexus/content/repositories/public/。（这个步骤在本例子中已经添加，无需重复操作。）
###2. 在settings.xml文件中的mirrorOf做如下修改，<mirrorOf>*,!mulesoft</mirrorOf>
###3. 再次mvn clean install即可成功。