package ca.uwo.csd.ai.nlp.kernel;

/**
 * <code>KernelManager</code> provides the custom kernel function to <code>svm</code>.
 * @author Syeed Ibn Faiz
 */
public class KernelManager {
    static private CustomKernel customKernel;

    public static CustomKernel getCustomKernel() {
        return customKernel;
    }

    /**
     * Registers the custom kernel
     * @param customKernel 
     */
    public static void setCustomKernel(CustomKernel customKernel) {
        KernelManager.customKernel = customKernel;
    }
    
}
