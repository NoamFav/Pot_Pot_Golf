package com.um_project_golf.Core.Rendering;

import com.um_project_golf.Core.Camera;
import com.um_project_golf.Core.Entity.Model;
import com.um_project_golf.Core.Lighting.DirectionalLight;
import com.um_project_golf.Core.Lighting.PointLight;
import com.um_project_golf.Core.Lighting.SpotLight;

/**
 * The interface for the renderer.
 *
 * @param <T> The type of the renderer.
 */
public interface IRenderer<T> {
    void init() throws Exception;
    void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight);
    void cleanup();

    void bind(Model model);
    void unbind();
    void prepare(T t, Camera camera);
}
