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

struct Bgra {
    b: u8,
    g: u8,
    r: u8,
    a: u8,
}

impl Bgra {
    fn grayscale(self: &mut Self) {
        let gray = ((0.299 * self.r as f32) +
            (0.587 * self.g as f32) +
            (0.114 * self.b as f32)) as u8;
        self.r = gray;
        self.g = gray;
        self.b = gray;
    }

    fn invert_pixel(self: &mut Self) {
        self.r = 255 - self.r;
        self.g = 255 - self.g;
        self.b = 255 - self.b;
    }
}

#[cfg(target_os = "android")]
#[allow(non_snake_case)]
pub mod android {
    use std::slice;
    use jni::JNIEnv;
    use jni::objects::{JByteBuffer, JClass, JObject};
    use jni::sys::{jint, jstring};
    use crate::{add, get_architecture, Bgra};

    // The native function implemented in Rust.
    #[unsafe(no_mangle)]
    pub extern "C" fn Java_com_kshitijpatil_rustique_Rustique_add(
        left: u64,
        right: u64,
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
    pub extern "C" fn Java_com_kshitijpatil_rustique_Rustique_grayscale(
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
        let pixels_per_row = stride as usize / size_of::<Bgra>();
        // Create a mutable slice from the buffer
        let mut data = unsafe { slice::from_raw_parts_mut(buffer_addr as *mut Bgra, buffer_capacity) };
        // Iterate over each pixel in the image
        for y in 0..height as usize {
            for x in 0..width as usize {
                let pixel_idx = y * pixels_per_row + x;

                if pixel_idx < data.len() {
                    data[pixel_idx].grayscale()
                }
            }
        }
    }

    #[unsafe(no_mangle)]
    pub extern "C" fn Java_com_kshitijpatil_rustique_Rustique_invert(
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
        let pixels_per_row = stride as usize / size_of::<Bgra>();
        // Create a mutable slice from the buffer
        let mut data = unsafe { slice::from_raw_parts_mut(buffer_addr as *mut Bgra, buffer_capacity) };
        // Iterate over each pixel in the image
        for y in 0..height as usize {
            for x in 0..width as usize {
                let pixel_idx = y * pixels_per_row + x;

                if pixel_idx < data.len() {
                    data[pixel_idx].invert_pixel()
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
