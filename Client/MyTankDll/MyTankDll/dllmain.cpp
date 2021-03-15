// dllmain.cpp : Defines the entry point for the DLL application.
#include "stdafx.h"
#include <jni.h>
#include "mytank_MyTank.h"

BOOL APIENTRY DllMain( HMODULE hModule,
                       DWORD  ul_reason_for_call,
                       LPVOID lpReserved
					 )
{
	switch (ul_reason_for_call)
	{
	case DLL_PROCESS_ATTACH:
	case DLL_THREAD_ATTACH:
	case DLL_THREAD_DETACH:
	case DLL_PROCESS_DETACH:
		break;
	}
	return TRUE;
}

JNIEXPORT jbyteArray JNICALL Java_mytank_MyTank_doSomething(JNIEnv *env, jobject obj, jint posX, jint posY, jobjectArray mapArr) {
	/*
	
	
	Your code should be here!
	Method params explanations :
	- posX, posY : Your tank position in map
	- mapArr : one dimension array that contains the map 
			- value 0 for road
			- value 1 for red wall (destructible wall)
			- value 2 for silver wall (undestrictible wall)
			- value 3 for water (tank cannot drive through it)
			- value 4 for our tank
			- value 5 for other tank
			- value 6 for our base
			- value 7 for the enemy base
	Return value explanation :
		This method will return a byte array which contain the commands
		array[0] for move
		array[1] for shoot
		
	
	Your task is to make an AI for the tank to shoot or move
	
	*/
	printf("posX %d, posY %d\n", posX, posY);
	jbyte a[] = {posX, posY};
	jbyteArray ret = env->NewByteArray(2);
	env->SetByteArrayRegion(ret, 0, 2, a);

	return ret;
}
