package com.packforyou.ui.utils

import com.google.android.gms.common.internal.safeparcel.SafeParcelable

class PermutationsIteratively(arr:Array<Byte>) {

    // The input array for permutation
    private var Arr = arr

    // Index array to store indexes of input array
    private var Indexes = ByteArray(Arr.size)

    // The index of the first "increase"
    // in the Index array which is the smallest
    // i such that Indexes[i] < Indexes[i + 1]
    private var Increase = -1

    // Initialize and output
    // the first permutation
    fun GetFirst(): ArrayList<Byte> {

        // Allocate memory for Indexes array
        Indexes = ByteArray(Arr.size)

        // Initialize the values in Index array
        // from 0 to n - 1
        for (i in Indexes.indices) {
            Indexes[i] = i.toByte()
        }

        // Set the Increase to 0
        // since Indexes[0] = 0 < Indexes[1] = 1
        Increase = 0

        // Output the first permutation
        return Output()
    }

    // Function that returns true if it is
    // possible to generate the next permutation
    fun HasNext(): Boolean {

        // When Increase is in the end of the array,
        // it is not possible to have next one
        return Increase != Indexes.size - 1
    }

    // Output the next permutation
    fun GetNext(): ArrayList<Byte> {

        // Increase is at the very beginning
        if (Increase == 0) {

            // Swap Index[0] and Index[1]
            Swap(Increase, Increase + 1)

            // Update Increase
            Increase += 1
            while (Increase < Indexes.size - 1
                && Indexes[Increase]
                > Indexes[Increase + 1]
            ) {
                ++Increase
            }
        } else {

            // Value at Indexes[Increase + 1] is greater than Indexes[0]
            // no need for binary search,
            // just swap Indexes[Increase + 1] and Indexes[0]
            if (Indexes[Increase + 1] > Indexes[0]) {
                Swap(Increase + 1, 0)
            } else {

                // Binary search to find the greatest value
                // which is less than Indexes[Increase + 1]
                var start = 0
                var end = Increase
                var mid = (start + end) / 2
                val tVal = Indexes[Increase + 1]
                while (!(Indexes[mid] < tVal && Indexes[mid - 1] > tVal)) {
                    if (Indexes[mid] < tVal) {
                        end = mid - 1
                    } else {
                        start = mid + 1
                    }
                    mid = (start + end) / 2
                }

                // Swap
                Swap(Increase + 1, mid)
            }

            // Invert 0 to Increase
            for (i in 0..Increase / 2) {
                Swap(i, Increase - i)
            }

            // Reset Increase
            Increase = 0
        }
        return Output()
    }

    // Function to output the input array
    private fun Output(): ArrayList<Byte> {
        val array = arrayListOf<Byte>()
        for (i in Indexes.indices) {
            // Indexes of the input array
            // are at the Indexes array
            array.add(Arr[Indexes[i].toInt()])
        }
        return array
    }

    // Swap two values in the Indexes array
    private fun Swap(p: Int, q: Int) {
        val tmp = Indexes[p]
        Indexes[p] = Indexes[q]
        Indexes[q] = tmp
    }
}