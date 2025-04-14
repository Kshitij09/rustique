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
#[cfg(target_os = "android")]
#[allow(non_snake_case)]
pub mod android {
    use jni::objects::JClass;
    use jni::JNIEnv;
    use jni::sys::jstring;
    use crate::{add, get_architecture};

    // The native function implemented in Rust.
    #[unsafe(no_mangle)]
    pub unsafe extern "C" fn Java_com_kshitijpatil_rustique_Rustique_add(
        _: JNIEnv,
        _: JClass,
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
