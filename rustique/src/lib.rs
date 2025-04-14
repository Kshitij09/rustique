mod image;

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

#[cfg(target_os = "android")]
#[allow(non_snake_case)]
pub mod android {
    use std::slice;
    use jni::JNIEnv;
    use jni::objects::{JByteBuffer, JClass};
    use jni::sys::{jint, jstring};
    use crate::get_architecture;
    use crate::image::Bgra;

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
        height: jint,
        stride: jint,
    ) {
        let buffer_addr = unsafe { env.get_direct_buffer_address(&buffer).unwrap() };
        let buffer_capacity = (stride * height) as usize;
        let pixels = unsafe { slice::from_raw_parts_mut(buffer_addr as *mut Bgra, buffer_capacity) };
        for px in pixels {
            px.grayscale()
        }
    }

    #[unsafe(no_mangle)]
    pub extern "C" fn Java_com_kshitijpatil_rustique_Rustique_invert(
        env: JNIEnv,
        _class: JClass,
        buffer: JByteBuffer,
        height: jint,
        stride: jint,
    ) {
        let buffer_addr = unsafe { env.get_direct_buffer_address(&buffer).unwrap() };
        let buffer_capacity = (stride * height) as usize;
        let pixels = unsafe { slice::from_raw_parts_mut(buffer_addr as *mut Bgra, buffer_capacity) };
        for px in pixels {
            px.invert()
        }
    }
}