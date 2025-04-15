# High Level Flow

1. On one hand, we have rust only implementations
    ```rust
    pub struct Bgra { b: u8, g: u8, r: u8, a: u8 }
    pub fn grayscale(self: &mut Bgra) {
        let gray = ((0.299 * self.r as f32) +
            (0.587 * self.g as f32) +
            (0.114 * self.b as f32)) as u8;
        self.r = gray;
        self.g = gray;
        self.b = gray;
    }
    ```
2. Which we expose through JNI bindings
    ```rust
    #[cfg(target_os = "android")]
    #[allow(non_snake_case)]
    pub mod android {
        #[unsafe(no_mangle)]
        pub extern "C" fn Java_com_kshitijpatil_rustique_Rustique_grayscale(
            env: JNIEnv,
            _class: JClass,
            buffer: JByteBuffer,
            height: jint,
            stride: jint,
        ) {
            // get pointer to native buffer
            let buffer_addr = unsafe { env.get_direct_buffer_address(&buffer).unwrap() };
            let buffer_capacity = (stride * height) as usize;
            // read buffer bytes into &[Bgra] slice
            let pixels = unsafe { slice::from_raw_parts_mut(buffer_addr as *mut Bgra, buffer_capacity) };
            // mutate Bgra struct with image manipulations
            for px in pixels {
                px.grayscale()
            }
        }
    }
    ```
3. Then we build dynamic libs (`*.so`) for android targets using `cargo build`
4. These libs (`*.so`) are then copied under `app/src/main/jniLibs` directory.
5. Next, we will add a library loader class in our app, matching the JNI signature in rust
```kotlin
class Rustique {
    companion object {
        init {
            System.loadLibrary("rustique")
        }
    }

    @JvmName("grayscale")
    external fun grayscale(buffer: ByteBuffer, height: Int, stride: Int)
}
```
6. At this point, we are good to use this class just as any other class in the project and it would work with the native bindings
7. We create mutable Bitmap and allocate a `ByteBuffer` to play around the image efficiently. Refer `ImageViewModel.kt` to learn more