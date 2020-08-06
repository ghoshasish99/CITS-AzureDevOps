pushd %~dp0
cd lib
FOR %%I in (selenium-server*.jar) DO SET SELENIUM=%%I 
java -jar %SELENIUM% -role node -nodeConfig ../nodeconfig.json
