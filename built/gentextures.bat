@echo off

set /p path=<javapath.txt
%path% -jar ConnectedTextureMaker.jar %*
