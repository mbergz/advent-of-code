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
