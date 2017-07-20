package darwin.jopenctm.compression;

import darwin.jopenctm.data.AttributeData;
import darwin.jopenctm.data.Mesh;

import java.util.ArrayList;
import java.util.List;

import static darwin.jopenctm.data.Mesh.*;

public class Mg2DataSort {
    private int[] idx;
    private Mesh mesh;

    public Mg2DataSort(int[] idx, Mesh mesh) {
        this.idx = idx;
        this.mesh = mesh;
    }

    private static class HighLow {
        final int low, high;

        private HighLow(int low, int high) {
            this.low = low;
            this.high = high;
        }
    }

    public void sort() {
        List<HighLow> workingSet = new ArrayList<>();
        workingSet.add(new HighLow(0, idx.length - 1));

        while (!workingSet.isEmpty()) {
            HighLow first = workingSet.remove(0);
            int low = first.low;
            int high = first.high;

            if (low >= high)
                continue;

            // pick the pivot
            int pivot = low + (high - low) / 2;

            // make left < pivot and right > pivot
            int i = low, j = high;
            while (i <= j) {
                while (compare(i, pivot) < 0) {
                    i++;
                }

                while (compare(j, pivot) > 0) {
                    j--;
                }

                if (i <= j) {
                    swap(i, j);

                    i++;
                    j--;
                }
            }

            // recursively sort two sub parts
            if (low < j){
                workingSet.add(new HighLow(low, j));
            }

            if (high > i){
                workingSet.add(new HighLow(i, high));
            }
        }
    }

    private int compare(int a, int b) {
        if (idx[a] != idx[b]) {
            return idx[a] - idx[b];
        }
        return (int) Math.signum(mesh.vertices[a * 3] - mesh.vertices[b * 3]);
    }

    private void swap(int a, int b) {
        {
            int temp = idx[b];
            idx[b] = idx[a];
            idx[a] = temp;
        }

        arraySwap(mesh.vertices, a, b, CTM_POSITION_ELEMENT_COUNT);
        if(mesh.hasNormals()){
            arraySwap(mesh.normals, a, b, CTM_NORMAL_ELEMENT_COUNT);
        }
        for (AttributeData uv : mesh.texcoordinates) {
            arraySwap(uv.values, a, b, CTM_UV_ELEMENT_COUNT);
        }
        for (AttributeData atts : mesh.attributes) {
            arraySwap(atts.values, a, b, CTM_ATTR_ELEMENT_COUNT);
        }
    }

    private void arraySwap(float[] data, int a, int b, int length) {
        for (int i = 0; i < length; i++) {
            arraySwap(data, a + i, b + i);
        }
    }

    private void arraySwap(float[] data, int a, int b) {
        float temp = data[b];
        data[b] = data[a];
        data[a] = temp;
    }
}
