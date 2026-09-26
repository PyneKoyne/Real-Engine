// Author: Kenny Z & Anish Nagariya
// Date: June 16th
// Program Name: Craft Me In
// Description: ArrayGPU creates an object which can interface with the GPU to perform an array of tasks simultaneously

package main;

// imports
import org.jocl.*;
import java.util.HashMap;
import static org.jocl.CL.*;

// ArrayGPU class to create a new ArrayGPU object. Heavily inspired by JOCL template code
public class ArrayGPU {
    // openCL code to find where a point projects onto the scren
    public static String projectionSource = "__kernel void " +
            "projectionKernel(__global const float *a," +
            "             __global const float *b," +
            "             __global int *c," +
            "             __global int *d," +
            "             __global int *e," +
            "             __global int *f," +
            "             __global int *g)" +
            "{" +
            "    int gid = get_global_id(0);" +
            "    float x = (a[gid * 3 + 0] - b[11]);" +
            "    float y = (a[gid * 3 + 1] - b[12]);" +
            "    float z = (a[gid * 3 + 2] - b[13]);" +
            "    float dot = mad(x, b[0], mad(y, b[1], z * b[2]));" +
            "    if (dot > 0) {" + /* only draws if the point is in front of the camera */
            "       float angle = clamp(acos(dot * native_rsqrt(mad(x, x, mad(y, y, z * z)))), -4.0, 4.0);" +
            "       float con = 2.0 * mad(b[3], x, mad(b[4], y, b[5] * z));" +
            "       float new_y = mad(b[4], con, mad(y, b[7], (mad(b[5], x, -b[3] * z)) * b[6] * 2.0));" +
            "       float new_z = mad(b[5], con, mad(z, b[7], (mad(b[3], y, -b[4] * x)) * b[6] * 2.0));" +
            "       float hyp = (angle * b[8] * 8192) * native_rsqrt(mad(new_y, new_y, new_z * new_z));" +
            "       int loc = clamp(mad(round(mad(new_z, hyp, b[10])), b[9] * 2, clamp(round(mad(new_y, hyp, b[9])), 0.0, 2 * b[9])), 0.0, b[9] * b[10] * 4);" +
            "       int color = c[gid];" +
            "       atomic_add(&d[loc], 1);" + /* stores the final screen location in one float */
            "       atomic_add(&e[loc], (color & 0xff0000) >> 16);" + /* stores the final screen location in one float */
            "       atomic_add(&f[loc], (color & 0xff00) >> 8);" + /* stores the final screen location in one float */
            "       atomic_add(&g[loc], (color & 0xff));" + /* stores the final screen location in one float */
            "    }" +
            "}";

    public static String collapseSource = "__kernel void " +
            "collapseKernel(__global int *cnt," +
            "             __global int *r," +
            "             __global int *g," +
            "             __global int *b," +
            "             __global int *out," +
            "             int bg)" +
            "{" +
            "    int gid = get_global_id(0);" +
            "    if (cnt[gid] == 0) { out[gid] = bg; }" +
            "    else {" +
            "       out[gid] = (255 << 24) | ((r[gid] / cnt[gid]) << 16) | ((g[gid] / cnt[gid]) << 8 | (b[gid]/cnt[gid]));" +
            "    }" +
            "}";

    // variables for the gpu
    private cl_context context;
    private cl_device_id device;
    private cl_kernel projection_kernel;
    private cl_kernel collapse_kernel;
    private cl_command_queue commandQueue;
    private HashMap<Integer, cl_mem[]> memObjects = new HashMap<>(); // hashmaps of the game objects to be drawn
    private cl_program program;
    private cl_mem camMem;
    private cl_mem cnt, r, g, b, out;
    private int set_width, set_height;

    // constructor which sets all the default information for the GPU
    public ArrayGPU(){
        // The platform, device type and device number
        // that will be used
        final int platformIndex = 0;
        final long deviceType = CL.CL_DEVICE_TYPE_ALL;
        final int deviceIndex = 0;

        // Obtain the number of platforms
        int[] numPlatformsArray = new int[1];
        System.out.println("clGetPlatformIDs err="+CL.clGetPlatformIDs(0, null, numPlatformsArray)+" numPlatforms="+numPlatformsArray[0]);
        CL.clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];

        // Obtain a platform ID
        cl_platform_id[] platforms = new cl_platform_id[numPlatforms];
        CL.clGetPlatformIDs(platforms.length, platforms, null);
        cl_platform_id platform = platforms[platformIndex];

        byte[] pbuf = new byte[1024];
        long[] psize = new long[1];
        CL.clGetPlatformInfo(platform, CL.CL_PLATFORM_NAME, pbuf.length, Pointer.to(pbuf), psize);
        System.out.println("Platform: " + new String(pbuf, 0, (int) psize[0]));

        // Initialize the context properties
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL.CL_CONTEXT_PLATFORM, platform);

        // Obtain the number of devices for the platform
        int[] numDevicesArray = new int[1];
        CL.clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        System.out.println("clGetDeviceIDs err="+CL.clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray)+" numDevices="+numDevicesArray[0]);
        int numDevices = numDevicesArray[0];

        // Obtain a device ID
        cl_device_id[] devices = new cl_device_id[numDevices];
        CL.clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        cl_device_id device = devices[deviceIndex];
        this.device = device;

        // Create a context for the selected device
        this.context = CL.clCreateContext(
                contextProperties, 1, new cl_device_id[]{device},
                null, null, null);

        // Create a command-queue for the selected device
        this.commandQueue =
                CL.clCreateCommandQueue(this.context, device, 0, null);
    }

    // Creates a program based on the source code
    public void startProgram(String programSource){
        this.program = CL.clCreateProgramWithSource(context,
                1, new String[]{programSource}, null, null);

        // Build the program
        int buildStatus = CL.clBuildProgram(this.program, 0, null, "-cl-fast-relaxed-math -cl-mad-enable", null, null);
        if (buildStatus != 0) {
            System.err.println("OpenCL kernel build failed (status " + buildStatus + ")");
            System.err.println("OpenCL build log:\n" + getBuildLog());
        }

        // Create the kernel
        this.projection_kernel = CL.clCreateKernel(this.program, "projectionKernel", null);
        this.collapse_kernel = CL.clCreateKernel(this.program, "collapseKernel", null);
        this.camMem = CL.clCreateBuffer(this.context,
                CL.CL_MEM_READ_ONLY,
                (long) Sizeof.cl_float * 14, null, null);
    }

    // Returns the OpenCL compiler log for the current program (never throws)
    private String getBuildLog() {
        try {
            // Query the required log size first
            long[] logSize = new long[1];
            CL.clGetProgramBuildInfo(this.program, this.device, CL.CL_PROGRAM_BUILD_LOG, 0, null, logSize);
            int size = (int) logSize[0];
            if (size <= 1) {
                return "(empty build log)";
            }
            // Fetch the log itself
            byte[] logData = new byte[size];
            CL.clGetProgramBuildInfo(this.program, this.device, CL.CL_PROGRAM_BUILD_LOG, size, Pointer.to(logData), null);
            return new String(logData, 0, size, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "(could not retrieve build log: " + e + ")";
        }
    }

    public void setPixelData(int width, int height) {
        if (cnt != null && height == set_height && width == set_width) {
            return;
        } else if (cnt != null) {
            CL.clReleaseMemObject(cnt);
            CL.clReleaseMemObject(r);
            CL.clReleaseMemObject(g);
            CL.clReleaseMemObject(b);
            CL.clReleaseMemObject(out);
        }
        set_height = height;
        set_width = width;
        long bytes = (long) width * height * Sizeof.cl_int;
        cnt = CL.clCreateBuffer(this.context, CL.CL_MEM_READ_WRITE, bytes, null, null);
        r = CL.clCreateBuffer(this.context, CL.CL_MEM_READ_WRITE, bytes, null, null);
        g = CL.clCreateBuffer(this.context, CL.CL_MEM_READ_WRITE, bytes, null, null);
        b = CL.clCreateBuffer(this.context, CL.CL_MEM_READ_WRITE, bytes, null, null);
        out = CL.clCreateBuffer(this.context, CL.CL_MEM_READ_WRITE, bytes, null, null);
    }

    // allocates memory objects to save time if they are used between seperate runs
    public void allocateMemory(int n, float[] srcArrayA, int[] srcArrayC, int hash){
        // Allocate the memory objects for the input and output data
        Pointer srcA = Pointer.to(srcArrayA);
        Pointer srcC = Pointer.to(srcArrayC);
        cl_mem[] mem = new cl_mem[2];
        mem[0] = CL.clCreateBuffer(this.context,
                CL.CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                (long) Sizeof.cl_float * n, srcA, null);
        mem[1] = CL.clCreateBuffer(this.context,
                CL.CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                (long) Sizeof.cl_int * n, srcC, null);
        memObjects.put(hash, mem);
    }

    // removes memory objects when a gameobject is deloaded
    public void unallocateMemory(int hash){
        // Allocate the memory objects for the input and output data
        if (memObjects.containsKey(hash)) {
            cl_mem[] mem = memObjects.get(hash);
            CL.clReleaseMemObject(mem[0]);
            CL.clReleaseMemObject(mem[1]);
            CL.clReleaseMemObject(camMem);
            memObjects.remove(hash);
        }
    }

    // sets the memory data of the camera details
    public void setCamMem(float[] srcArrayB){
        Pointer srcB = Pointer.to(srcArrayB);

        clEnqueueWriteBuffer(commandQueue, this.camMem, CL_TRUE, 8 * Sizeof.cl_float,
                (long) 3 * Sizeof.cl_float, srcB, 0, null, null);
    }

    // Executes the program based on the input received and the other stored memory objects
    public void projectVectors(float[] focal, float[] norm, float[] rot, int ids, int hash) {
        Pointer srcB = Pointer.to(focal);
        Pointer srcN = Pointer.to(norm);
        Pointer srcR = Pointer.to(rot);

        cl_mem[] mem = this.memObjects.get(hash);
        if(mem == null){
            return;
        }
        clEnqueueWriteBuffer(commandQueue, this.camMem, CL_TRUE, 0,
                (long) 3 * Sizeof.cl_float, srcN, 0, null, null);
        clEnqueueWriteBuffer(commandQueue, this.camMem, CL_TRUE, 3 * Sizeof.cl_float,
                (long) 5 * Sizeof.cl_float, srcR, 0, null, null);
        clEnqueueWriteBuffer(commandQueue, this.camMem, CL_TRUE, 11 * Sizeof.cl_float,
                (long) 3 * Sizeof.cl_float, srcB, 0, null, null);

        // Set the arguments for the kernel
        CL.clSetKernelArg(this.projection_kernel, 0,
                Sizeof.cl_mem, Pointer.to(mem[0]));
        CL.clSetKernelArg(this.projection_kernel, 1,
                Sizeof.cl_mem, Pointer.to(this.camMem));
        CL.clSetKernelArg(this.projection_kernel, 2,
                Sizeof.cl_mem, Pointer.to(mem[1]));
        CL.clSetKernelArg(this.projection_kernel, 3,
                Sizeof.cl_mem, Pointer.to(cnt));
        CL.clSetKernelArg(this.projection_kernel, 4,
                Sizeof.cl_mem, Pointer.to(r));
        CL.clSetKernelArg(this.projection_kernel, 5,
                Sizeof.cl_mem, Pointer.to(g));
        CL.clSetKernelArg(this.projection_kernel, 6,
                Sizeof.cl_mem, Pointer.to(b));

        // Set the work-item dimensions
        long[] global_work_size = new long[]{ids};

        // Execute the kernel
        CL.clEnqueueNDRangeKernel(this.commandQueue, this.projection_kernel, 1, null,
                global_work_size, null, 0, null, null);
    }

    public void clearScreen(int m) {
        clEnqueueFillBuffer(commandQueue, cnt, Pointer.to(new int[]{0}), Sizeof.cl_int, 0, (long) m * Sizeof.cl_int, 0, null, null);
        clEnqueueFillBuffer(commandQueue, r, Pointer.to(new int[]{0}), Sizeof.cl_int, 0, (long) m * Sizeof.cl_int, 0, null, null);
        clEnqueueFillBuffer(commandQueue, g, Pointer.to(new int[]{0}), Sizeof.cl_int, 0, (long) m * Sizeof.cl_int, 0, null, null);
        clEnqueueFillBuffer(commandQueue, b, Pointer.to(new int[]{0}), Sizeof.cl_int, 0, (long) m * Sizeof.cl_int, 0, null, null);
    }

    public void collapseColours(int m, int bg, int[] pixelData) {
        Pointer pixelDataPointer = Pointer.to(pixelData);
        clSetKernelArg(collapse_kernel, 0, Sizeof.cl_mem, Pointer.to(cnt));
        clSetKernelArg(collapse_kernel, 1, Sizeof.cl_mem, Pointer.to(r));
        clSetKernelArg(collapse_kernel, 2, Sizeof.cl_mem, Pointer.to(g));
        clSetKernelArg(collapse_kernel, 3, Sizeof.cl_mem, Pointer.to(b));
        clSetKernelArg(collapse_kernel, 4, Sizeof.cl_mem, Pointer.to(out));
        clSetKernelArg(collapse_kernel, 5, Sizeof.cl_int, Pointer.to(new int[]{bg}));
        clEnqueueNDRangeKernel(commandQueue, collapse_kernel, 1, null, new long[]{m}, null, 0, null, null);
        clEnqueueReadBuffer(commandQueue, out, CL_TRUE, 0, (long) m * Sizeof.cl_int, pixelDataPointer, 0, null, null);
    }

    // closes the GPU and releases all memory objects
    public void closeGPU(){
        // Release kernel, program, and memory objects
        CL.clReleaseMemObject(camMem);
        for (cl_mem[] mem: memObjects.values()) {
            CL.clReleaseMemObject(mem[0]);
            CL.clReleaseMemObject(mem[1]);
            CL.clReleaseMemObject(mem[2]);
            CL.clReleaseMemObject(mem[3]);
            CL.clReleaseMemObject(mem[4]);
            CL.clReleaseMemObject(mem[5]);
        }
        CL.clReleaseMemObject(camMem);
        CL.clReleaseContext(this.context);
        CL.clReleaseCommandQueue(this.commandQueue);
        CL.clReleaseKernel(this.projection_kernel);
        clReleaseProgram(this.program);
    }
}