#pragma once
#include <iostream>
#include "Vector3.h"
#include <vector>
using namespace std;

class Face
{
public:
	uint32_t ix1;
	uint32_t ix2;
	uint32_t ix3;
	// jäsenfunktio joka laskee kolmion pinta-alan
	float CountArea(const Face& face, vector<Vertex> vertexes);
};