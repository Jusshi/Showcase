#include <iostream>
#include <cmath>
#include "Vector3.h"

using namespace std;

//konstruktori
Vector3::Vector3() {

}
// kopiomuodostin
Vector3::Vector3(const Vector3& v)
{
	X = v.X;
	Y = v.Y;
	Z = v.Z;
}
//Lisäys jäsenfunktio
Vector3
Vector3::Add(const Vector3& v)
{
	Vector3 result;
	result.X = X + v.X;
	result.Y = Y + v.Y;
	result.Z = Z + v.Z;
	return result;
}
//vähennys jäsenfunktio
Vector3
Vector3::Decrease(const Vector3& v)
{
	Vector3 result;
	result.X = X - v.X;
	result.Y = Y - v.Y;
	result.Z = Z - v.Z;
	return result;
}
// Skaalarilla kertomis jäsenfunktio
Vector3
Vector3::Multiply(const Vector3& v, float skaalari)
{
	Vector3 result;
	result.X = skaalari * v.X;
	result.Y = skaalari * v.Y;
	result.Z = skaalari * v.Z;
	return result;
}
// Skaalarilla jakamis jäsenfunktio
Vector3
Vector3::Division(const Vector3& v, float skaalari)
{
	Vector3 result;
	result.X = v.X / skaalari;
	result.Y = v.Y / skaalari;
	result.Z = v.Z / skaalari;
	return result;
}
//Vektorin pituuden lasku jäsenfunktio
float
Vector3::Magnitude()
{
	float Mag = sqrt(pow(X, 2) + pow(Y, 2) + pow(Z, 2));
	return Mag;
}
// + operaattorin ylikirjoitus
Vector3
Vector3::operator+(const Vector3& v)
{
	return Add(v);
}
// - operaattorin ylikirjoitus
Vector3
Vector3::operator-(const Vector3& v)
{
	return Decrease(v);
}