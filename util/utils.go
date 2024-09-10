package util

import "strconv"

func ToInt(stringVal string) int {
	res, err := strconv.Atoi(stringVal)
	if err != nil {
		panic(err)
	}
	return res
}
