package com.pot_pot_golf.Core.Rendering;

import com.pot_pot_golf.Core.Camera;
import com.pot_pot_golf.Core.Entity.Model;
import com.pot_pot_golf.Core.Lighting.DirectionalLight;
import com.pot_pot_golf.Core.Lighting.PointLight;
import com.pot_pot_golf.Core.Lighting.SpotLight;

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
