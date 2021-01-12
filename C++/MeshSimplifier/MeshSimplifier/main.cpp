#include <iostream>
#include "Vector3.h"
#include "Mesh.h"
using namespace std;

int main(int argc, char** argv)
{
	shared_ptr<ObjHandler> m = shared_ptr<ObjHandler>(new ObjHandler());
	// Otetaan kiinni mahdolliset virheet syötearvojen muuntamisessa
	try
	{
		m->ReadObjFile(argv[1]);
	}
	catch (ImproperValueException& error)
	{
		cerr << error.what() << "\n";
	}
	// poistetaan pienin kolmio
	m->EraseSmallest();
	// luodaan meshin datasta uusi .obj tiedosto
	m->WriteObjFile("AmazingNewObject.obj");
	return 0;
}