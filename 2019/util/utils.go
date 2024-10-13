package util

import (
	"fmt"
	"runtime"
	"strconv"
	"time"
)

func ToInt(stringVal string) int {
	res, err := strconv.Atoi(stringVal)
	if err != nil {
		panic(err)
	}
	return res
}

func AbsInt(x int) int {
	if x < 0 {
		return -x
	}
	return x
}

/**
 * LCM using GCD formula
 *
 * lcm(a,b,c) = lcm(a,lcm(b,c))
 */
func Lcm(nbrs []int) int {
	if len(nbrs) == 2 {
		return lcmInternal(nbrs[0], nbrs[1])
	}
	return lcmInternal(nbrs[0], Lcm(nbrs[1:]))
}

func lcmInternal(a int, b int) int {
	return (a * b) / gcd(a, b)
}

// https://en.wikipedia.org/wiki/Euclidean_algorithm
func gcd(a int, b int) int {
	for b != 0 {
		temp := b
		b = a % b
		a = temp
	}
	return a
}

func Timer() func() {
	startTime := time.Now()

	pc, _, _, ok := runtime.Caller(1)
	var callingFnName string
	details := runtime.FuncForPC(pc)
	if ok && details != nil {
		callingFnName = details.Name()
	}

	return func() {
		fmt.Printf("%s took %s\n", callingFnName, time.Since(startTime))
	}
}
