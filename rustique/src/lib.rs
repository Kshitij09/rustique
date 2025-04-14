pub fn add(left: u64, right: u64) -> u64 {
    left + right
}

pub fn get_architecture() -> String {
    let arch = match std::env::consts::ARCH {
        "x86" => "x86",
        "x86_64" => "x86_64",
        "arm" => "arm",
        "aarch64" => "aarch64",
        _ => "unknown"
    };
    arch.to_string()
}

// Helper function to invert a pixel's B, G, R channels
fn invert_pixel(data: &mut [u8], pixel_start: usize) {
    data[pixel_start] = 255 - data[pixel_start];     // B
    data[pixel_start + 1] = 255 - data[pixel_start + 1]; // G
    data[pixel_start + 2] = 255 - data[pixel_start + 2]; // R
    // Alpha channel (pixel_start + 3) remains unchanged
}

fn grayscale(data: &mut[u8], pixel_start: usize) {
    let (b,g,r) = (data[pixel_start], data[pixel_start+1], data[pixel_start+2]);
    let gray = ((0.299 * r as f32) +
        (0.587 * g as f32) +
        (0.114 * b as f32)) as u8;
    data[pixel_start] = gray;
    data[pixel_start+1] = gray;
    data[pixel_start+2] = gray;
}

#[cfg(target_os = "android")]
#[allow(non_snake_case)]
pub mod android {
    use std::slice;
    use jni::JNIEnv;
    use jni::objects::{JByteBuffer, JClass, JObject};
    use jni::sys::{jint, jobject, jstring};
    use crate::{add, get_architecture, grayscale, invert_pixel};

    // The native function implemented in Rust.
    #[unsafe(no_mangle)]
    pub extern "C" fn Java_com_kshitijpatil_rustique_Rustique_add(
        left: u64,
        right: u64
    ) -> u64 {
        add(left, right)
    }

    #[unsafe(no_mangle)]
    pub extern "C" fn Java_com_kshitijpatil_rustique_Rustique_getArchitecture(
        env: JNIEnv,
    ) -> jstring {
        let arch = get_architecture();
        env.new_string(arch)
            .expect("couldn't create java string")
            .into_raw()
    }

    #[unsafe(no_mangle)]
    pub extern "system" fn Java_com_kshitijpatil_rustique_Rustique_processBitmap(
        env: JNIEnv,
        _class: JClass,
        buffer: JByteBuffer,
        width: jint,
        height: jint,
        stride: jint,
    ) {
        // Get direct ByteBuffer address
        let buffer_addr = unsafe { env.get_direct_buffer_address(&buffer).unwrap() };
        let buffer_capacity = (stride * height) as usize;
        // Create a mutable slice from the buffer
        let mut data = unsafe { slice::from_raw_parts_mut(buffer_addr, buffer_capacity) };
        // Iterate over each pixel in the image
        for y in 0..height as usize {
            for x in 0..width as usize {
                let pixel_start = y * stride as usize + x * 4;

                // Check if we are within bounds
                if pixel_start + 3 < data.len() {
                    // Invert B, G, R channels and leave A unchanged
                    grayscale(&mut data, pixel_start);
                }
            }
        }
    }
}



#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn it_works() {
        let result = add(2, 2);
        assert_eq!(result, 4);
    }
}
